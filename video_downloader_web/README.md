# App Web de Download de Vídeos (Python)

Aplicativo web simples em Flask para baixar vídeos a partir do link da página.

## Como executar

```bash
cd video_downloader_web
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
python app.py
```

Abra no navegador: `http://localhost:5000`

## Link de acesso ao aplicativo

- A página inicial mostra automaticamente o link atual de acesso do servidor.
- Também é possível consultar o link via API:

```bash
curl http://localhost:5000/link
```

Resposta esperada:

```json
{"access_link":"http://localhost:5000"}
```

## Testes

```bash
cd video_downloader_web
PYTHONPATH=. pytest -q
```

> Observação: baixe apenas conteúdo para o qual você tenha permissão.
