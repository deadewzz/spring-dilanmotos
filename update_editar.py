import os
import re

html_template = """<!DOCTYPE html>
<html lang="es" xmlns:th="http://www.thymeleaf.org">

<head>
    <meta charset="UTF-8">
    <title>{TITLE}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Estilos -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            display: flex;
            min-height: 100vh;
        }

        .sidebar {
            width: 250px;
            background-color: #f8f9fa;
            padding-top: 1rem;
            border-right: 1px solid #dee2e6;
        }

        .sidebar .nav-link {
            color: #333;
            padding: 10px 20px;
        }

        .sidebar .nav-link:hover {
            background-color: #e9ecef;
        }

        .main-content {
            flex-grow: 1;
            padding: 2rem;
        }
    </style>
</head>

<body>

    <!-- Menú lateral (fragmento) -->
    <div th:replace="menu :: sidebar"></div>

    <!-- Contenido principal -->
    <div class="main-content">
{INNER_BODY}
    </div>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>
"""

folder = r"src/main/resources/templates"

# Entidades a migrar a /admin/...
entities = ['usuario', 'tipoServicio', 'categoria', 'moto', 'marca', 'mecanico', 'cotizacion', 'historial', 'pqrs', 'caracteristicas', 'productos', 'servicio']
pattern_th = re.compile(r'th:(href|action)="@\{/(' + '|'.join(entities) + r')\b')
pattern_attr = re.compile(r'\b(href|action)="/(' + '|'.join(entities) + r')\b')

count = 0
for filename in os.listdir(folder):
    if filename.startswith("editar_") and filename.endswith(".html"):
        filepath = os.path.join(folder, filename)
        with open(filepath, "r", encoding="utf-8") as f:
            content = f.read()
            
        if "main-content" in content and "menu :: sidebar" in content:
            continue
            
        title_match = re.search(r"<title>(.*?)</title>", content)
        title = title_match.group(1) if title_match else "Editar"
        
        body_match = re.search(r"<body[^>]*>(.*?)</body>", content, re.DOTALL | re.IGNORECASE)
        if body_match:
            inner_body = body_match.group(1).strip()
            
            # Quitar scripts antiguos
            inner_body = re.sub(r'<script[^>]*>.*?</script>', '', inner_body, flags=re.DOTALL)
            
            # Reemplazar rutas a /admin/
            inner_body = pattern_th.sub(r'th:\1="@{/admin/\2', inner_body)
            inner_body = pattern_attr.sub(r'\1="/admin/\2', inner_body)
            
            new_content = html_template.replace("{TITLE}", title).replace("{INNER_BODY}", inner_body)
            
            with open(filepath, "w", encoding="utf-8") as f:
                f.write(new_content)
            print("Updated:", filename)
            count += 1
        else:
            print(f"No match for body in {filename}")

print(f"Total updated: {count}")
