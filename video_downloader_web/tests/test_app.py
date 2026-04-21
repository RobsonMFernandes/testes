import app as app_module


def test_index_page_loads() -> None:
    client = app_module.app.test_client()
    response = client.get("/")

    assert response.status_code == 200
    assert b"Baixar video por link" in response.data or b"Baixar v\xc3\xaddeo por link" in response.data
    assert b"Link de acesso ao aplicativo" in response.data


def test_access_link_endpoint_returns_json() -> None:
    client = app_module.app.test_client()
    response = client.get("/link", base_url="http://127.0.0.1:5000")

    assert response.status_code == 200
    assert response.is_json
    assert response.get_json() == {"access_link": "http://127.0.0.1:5000"}


def test_download_with_empty_url_shows_error() -> None:
    client = app_module.app.test_client()
    response = client.post("/download", data={"video_url": ""})

    assert response.status_code == 200
    assert b"Informe um link" in response.data


def test_download_success_message(monkeypatch) -> None:
    def fake_download_video(_url: str) -> str:
        return "downloads/test-video.mp4"

    monkeypatch.setattr(app_module, "download_video", fake_download_video)

    client = app_module.app.test_client()
    response = client.post("/download", data={"video_url": "https://example.com/video"})

    assert response.status_code == 200
    assert b"Download conclu" in response.data
