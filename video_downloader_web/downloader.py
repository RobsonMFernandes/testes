from __future__ import annotations

import os
from pathlib import Path
from urllib.parse import urlparse


def is_valid_http_url(url: str) -> bool:
    """Validate whether the provided string looks like an HTTP/HTTPS URL."""
    if not url:
        return False

    parsed = urlparse(url)
    return parsed.scheme in {"http", "https"} and bool(parsed.netloc)


def download_video(video_url: str, output_dir: str = "downloads") -> str:
    """Download a video URL into output_dir and return the resulting file path.

    Uses yt-dlp under the hood and raises ValueError for invalid input.
    """
    if not is_valid_http_url(video_url):
        raise ValueError("URL inválida. Use um link HTTP/HTTPS válido.")

    try:
        import yt_dlp
    except ImportError as exc:
        raise RuntimeError(
            "Dependência ausente: instale 'yt-dlp' para baixar vídeos."
        ) from exc

    output_path = Path(output_dir)
    output_path.mkdir(parents=True, exist_ok=True)

    output_template = str(output_path / "%(title)s-%(id)s.%(ext)s")

    options = {
        "outtmpl": output_template,
        "noplaylist": True,
        "quiet": True,
        "no_warnings": True,
    }

    with yt_dlp.YoutubeDL(options) as ydl:
        info = ydl.extract_info(video_url, download=True)
        file_path = ydl.prepare_filename(info)

    if not os.path.exists(file_path):
        # In alguns casos pós-processamento muda a extensão final.
        # Usamos o caminho informado pelo yt-dlp como melhor esforço.
        return file_path

    return file_path
