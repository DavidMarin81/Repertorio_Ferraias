# Despliegue en Hetzner

## 1. Publicar la API

Desde tu equipo:

```powershell
dotnet publish .\RepertorioFerraias\RepertorioFerraias.csproj -c Release -o .\publish
```

Sube la carpeta `publish` al VPS, por ejemplo a:

```text
/var/www/repertorio-ferraias
```

## 2. Preparar el VPS

Ejemplo para Ubuntu:

```bash
sudo apt update
sudo apt install -y nginx
```

Instala .NET 8 Runtime en el servidor siguiendo Microsoft:

```bash
sudo apt install -y dotnet-runtime-8.0
```

## 3. Configurar la API como servicio

1. Copia `deploy/repertorioferraias.service` a:

```text
/etc/systemd/system/repertorioferraias.service
```

2. Edita esta línea y pon tu cadena real de Mongo:

```text
Environment=MongoDb__ConnectionString=...
```

3. Arranca el servicio:

```bash
sudo systemctl daemon-reload
sudo systemctl enable repertorioferraias
sudo systemctl start repertorioferraias
sudo systemctl status repertorioferraias
```

Para ver logs:

```bash
journalctl -u repertorioferraias -f
```

## 4. Configurar Nginx

1. Copia `deploy/nginx.repertorioferraias.conf` a:

```text
/etc/nginx/sites-available/repertorioferraias
```

2. Cambia `api.tu-dominio.com` por tu dominio o subdominio.

3. Activa el sitio:

```bash
sudo ln -s /etc/nginx/sites-available/repertorioferraias /etc/nginx/sites-enabled/repertorioferraias
sudo nginx -t
sudo systemctl reload nginx
```

## 5. HTTPS con Let's Encrypt

```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d api.tu-dominio.com
```

## 6. Notas importantes

- En producción es mejor no guardar secretos en `appsettings.json`.
- Tu proyecto ya acepta variables de entorno como `MongoDb__ConnectionString`.
- Si mantienes la cadena actual en el repositorio, te recomiendo rotarla luego en MongoDB Atlas.
- Swagger ahora solo se abre en `Development`, así que en producción no aparecerá salvo que lo cambiemos.
