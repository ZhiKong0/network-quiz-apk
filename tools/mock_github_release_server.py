import argparse
import hashlib
import http.server
import json
import ssl
from datetime import datetime, timezone
from pathlib import Path
from urllib.parse import quote, unquote, urlparse

from cryptography import x509
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.x509.oid import NameOID


def ensure_dir(path: Path) -> Path:
    path.mkdir(parents=True, exist_ok=True)
    return path


def write_pem(path: Path, data: bytes) -> None:
    path.write_bytes(data)


def build_ca(cert_dir: Path):
    key_path = cert_dir / "ca_key.pem"
    cert_path = cert_dir / "ca_cert.pem"
    if key_path.exists() and cert_path.exists():
        return key_path, cert_path

    key = rsa.generate_private_key(public_exponent=65537, key_size=2048)
    subject = issuer = x509.Name([
        x509.NameAttribute(NameOID.COUNTRY_NAME, "CN"),
        x509.NameAttribute(NameOID.ORGANIZATION_NAME, "Codex Local Update Lab"),
        x509.NameAttribute(NameOID.COMMON_NAME, "Codex Local Update Root"),
    ])
    now = datetime.now(timezone.utc)
    cert = (
        x509.CertificateBuilder()
        .subject_name(subject)
        .issuer_name(issuer)
        .public_key(key.public_key())
        .serial_number(x509.random_serial_number())
        .not_valid_before(now)
        .not_valid_after(now.replace(year=now.year + 10))
        .add_extension(x509.BasicConstraints(ca=True, path_length=None), critical=True)
        .add_extension(x509.KeyUsage(
            digital_signature=True,
            key_encipherment=False,
            key_cert_sign=True,
            key_agreement=False,
            content_commitment=False,
            data_encipherment=False,
            encipher_only=False,
            decipher_only=False,
            crl_sign=True,
        ), critical=True)
        .sign(key, hashes.SHA256())
    )
    write_pem(
        key_path,
        key.private_bytes(
            serialization.Encoding.PEM,
            serialization.PrivateFormat.TraditionalOpenSSL,
            serialization.NoEncryption(),
        ),
    )
    write_pem(cert_path, cert.public_bytes(serialization.Encoding.PEM))
    return key_path, cert_path


def build_leaf(cert_dir: Path, ca_key_path: Path, ca_cert_path: Path, host: str):
    key_path = cert_dir / "server_key.pem"
    cert_path = cert_dir / "server_cert.pem"
    if key_path.exists() and cert_path.exists():
        return key_path, cert_path

    ca_key = serialization.load_pem_private_key(ca_key_path.read_bytes(), password=None)
    ca_cert = x509.load_pem_x509_certificate(ca_cert_path.read_bytes())
    key = rsa.generate_private_key(public_exponent=65537, key_size=2048)
    subject = x509.Name([
        x509.NameAttribute(NameOID.COUNTRY_NAME, "CN"),
        x509.NameAttribute(NameOID.ORGANIZATION_NAME, "Codex Local Update Lab"),
        x509.NameAttribute(NameOID.COMMON_NAME, host),
    ])
    now = datetime.now(timezone.utc)
    cert = (
        x509.CertificateBuilder()
        .subject_name(subject)
        .issuer_name(ca_cert.subject)
        .public_key(key.public_key())
        .serial_number(x509.random_serial_number())
        .not_valid_before(now)
        .not_valid_after(now.replace(year=now.year + 2))
        .add_extension(
            x509.SubjectAlternativeName([x509.DNSName(host)]),
            critical=False,
        )
        .add_extension(x509.BasicConstraints(ca=False, path_length=None), critical=True)
        .add_extension(x509.ExtendedKeyUsage([x509.oid.ExtendedKeyUsageOID.SERVER_AUTH]), critical=False)
        .sign(ca_key, hashes.SHA256())
    )
    write_pem(
        key_path,
        key.private_bytes(
            serialization.Encoding.PEM,
            serialization.PrivateFormat.TraditionalOpenSSL,
            serialization.NoEncryption(),
        ),
    )
    write_pem(cert_path, cert.public_bytes(serialization.Encoding.PEM))
    return key_path, cert_path


def subject_hash_old(cert_path: Path) -> str:
    cert = x509.load_pem_x509_certificate(cert_path.read_bytes())
    der_name = cert.subject.public_bytes()
    digest = hashlib.md5(der_name).digest()
    return f"{int.from_bytes(digest[:4], 'little'):08x}"


def prepare_android_ca(cert_dir: Path, ca_cert_path: Path) -> Path:
    hash_name = subject_hash_old(ca_cert_path) + ".0"
    android_ca = cert_dir / hash_name
    android_ca.write_bytes(ca_cert_path.read_bytes())
    return android_ca


def build_metadata(args) -> dict:
    apk_path = Path(args.apk).resolve()
    encoded_apk_name = quote(apk_path.name)
    data = {
        "packageName": args.package_name,
        "versionCode": args.version_code,
        "versionName": args.version_name,
        "apkFileName": apk_path.name,
        "apkDownloadUrl": f"https://{args.host}/assets/{encoded_apk_name}",
        "releaseNotes": args.release_notes,
        "generatedAt": datetime.now(timezone.utc).isoformat(),
    }
    return data


def build_release(args, metadata: dict) -> dict:
    apk_path = Path(args.apk).resolve()
    encoded_apk_name = quote(apk_path.name)
    return {
        "tag_name": "v" + args.version_name,
        "name": "v" + args.version_name,
        "html_url": f"https://{args.host}/html/local-test-release",
        "body": args.release_notes,
        "published_at": datetime.now(timezone.utc).isoformat(),
        "assets": [
            {
                "name": "exam-prep-handbook-update.json",
                "browser_download_url": f"https://{args.host}/assets/exam-prep-handbook-update.json",
                "size": 0,
            },
            {
                "name": apk_path.name,
                "browser_download_url": f"https://{args.host}/assets/{encoded_apk_name}",
                "size": apk_path.stat().st_size,
            },
        ],
    }


def make_handler(args, repo_info: dict, release: dict, metadata: dict):
    apk_path = Path(args.apk).resolve()
    release_bytes = json.dumps(release, ensure_ascii=False).encode("utf-8")
    repo_bytes = json.dumps(repo_info, ensure_ascii=False).encode("utf-8")
    metadata_bytes = json.dumps(metadata, ensure_ascii=False).encode("utf-8")
    notes_bytes = args.release_notes.encode("utf-8")

    class Handler(http.server.BaseHTTPRequestHandler):
        def log_message(self, format, *values):
            return

        def send_bytes(self, payload: bytes, content_type: str, status: int = 200):
            self.send_response(status)
            self.send_header("Content-Type", content_type)
            self.send_header("Content-Length", str(len(payload)))
            self.end_headers()
            self.wfile.write(payload)

        def do_GET(self):
            path = unquote(urlparse(self.path).path)
            if path == f"/repos/{args.repo_slug}":
                return self.send_bytes(repo_bytes, "application/json; charset=utf-8")
            if path == f"/repos/{args.repo_slug}/releases/latest":
                return self.send_bytes(release_bytes, "application/json; charset=utf-8")
            if path in ("/assets/exam-prep-handbook-update.json", "/assets/network_quiz_update.json"):
                return self.send_bytes(metadata_bytes, "application/json; charset=utf-8")
            if path == f"/assets/{apk_path.name}":
                payload = apk_path.read_bytes()
                return self.send_bytes(payload, "application/vnd.android.package-archive")
            if path == "/html/local-test-release":
                return self.send_bytes(notes_bytes, "text/plain; charset=utf-8")
            return self.send_bytes(b'{"message":"Not Found"}', "application/json; charset=utf-8", 404)

    return Handler


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--repo-slug", required=True)
    parser.add_argument("--apk", required=True)
    parser.add_argument("--package-name", required=True)
    parser.add_argument("--version-name", required=True)
    parser.add_argument("--version-code", required=True, type=int)
    parser.add_argument("--host", default="api.github.com")
    parser.add_argument("--port", default=8443, type=int)
    parser.add_argument("--work-dir", required=True)
    parser.add_argument("--release-notes", default="本地更新测试包")
    args = parser.parse_args()

    work_dir = ensure_dir(Path(args.work_dir).resolve())
    cert_dir = ensure_dir(work_dir / "certs")
    ca_key_path, ca_cert_path = build_ca(cert_dir)
    server_key_path, server_cert_path = build_leaf(cert_dir, ca_key_path, ca_cert_path, args.host)
    android_ca_path = prepare_android_ca(cert_dir, ca_cert_path)

    metadata = build_metadata(args)
    release = build_release(args, metadata)
    repo_info = {"full_name": args.repo_slug}

    info = {
        "ca_cert": str(ca_cert_path),
        "android_ca": str(android_ca_path),
        "server_cert": str(server_cert_path),
        "server_key": str(server_key_path),
        "repo_slug": args.repo_slug,
        "apk": str(Path(args.apk).resolve()),
        "version_name": args.version_name,
        "version_code": args.version_code,
        "port": args.port,
    }
    (work_dir / "server_info.json").write_text(json.dumps(info, ensure_ascii=False, indent=2), encoding="utf-8")

    handler = make_handler(args, repo_info, release, metadata)
    httpd = http.server.ThreadingHTTPServer(("127.0.0.1", args.port), handler)
    context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
    context.load_cert_chain(certfile=str(server_cert_path), keyfile=str(server_key_path))
    httpd.socket = context.wrap_socket(httpd.socket, server_side=True)
    print(json.dumps(info, ensure_ascii=False))
    httpd.serve_forever()


if __name__ == "__main__":
    main()
