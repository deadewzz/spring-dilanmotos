import os
import re

directory = r'c:\Users\coutt\Music\spring-dilanmotos\src\main\resources\templates'
entities = ['usuario', 'tipoServicio', 'categoria', 'moto', 'marca', 'mecanico', 'cotizacion', 'historial', 'pqrs', 'caracteristicas', 'productos', 'servicio']
pattern_th = re.compile(r'th:(href|action)="@\{/(' + '|'.join(entities) + r')\b')
pattern_attr = re.compile(r'\b(href|action)="/(' + '|'.join(entities) + r')\b')

count = 0
for root, dirs, files in os.walk(directory):
    for file in files:
        if file.endswith('.html'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()

            new_content = pattern_th.sub(r'th:\1="@{/admin/\2', content)
            new_content = pattern_attr.sub(r'\1="/admin/\2', new_content)

            if content != new_content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                count += 1
                print(f"Updated {file}")

print(f"Total files updated: {count}")
