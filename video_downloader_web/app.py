from __future__ import annotations

from flask import Flask, jsonify, render_template, request

from downloader import download_video

app = Flask(__name__)


def build_access_link() -> str:
    """Monta o link de acesso com base no host atual da requisição."""
    return request.host_url.rstrip("/")


@app.get("/")
def index():
    return render_template("index.html", access_link=build_access_link())


@app.get("/link")
def access_link():
    return jsonify({"access_link": build_access_link()})


@app.post("/download")
def download():
    video_url = request.form.get("video_url", "").strip()

    if not video_url:
        return render_template(
            "index.html",
            error="Informe um link de vídeo para continuar.",
            video_url=video_url,
            access_link=build_access_link(),
        )

    try:
        saved_file = download_video(video_url)
        return render_template(
            "index.html",
            success=f"Download concluído com sucesso: {saved_file}",
            video_url=video_url,
            access_link=build_access_link(),
        )
    except ValueError as exc:
        return render_template(
            "index.html",
            error=str(exc),
            video_url=video_url,
            access_link=build_access_link(),
        )
    except Exception as exc:  # noqa: BLE001 - exibimos mensagem amigável na UI
        return render_template(
            "index.html",
            error=f"Falha ao baixar o vídeo: {exc}",
            video_url=video_url,
            access_link=build_access_link(),
        )


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
