from downloader import is_valid_http_url


def test_is_valid_http_url_accepts_http_and_https() -> None:
    assert is_valid_http_url("https://example.com/video")
    assert is_valid_http_url("http://example.com/watch?v=1")


def test_is_valid_http_url_rejects_invalid_values() -> None:
    assert not is_valid_http_url("")
    assert not is_valid_http_url("ftp://example.com/file")
    assert not is_valid_http_url("apenas-texto")
