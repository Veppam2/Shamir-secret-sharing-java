# Esquema de Secreto Compartido de Shamir.

### Integrantes:
 - Carlos Daniel Cortés Jiménez.
 - Víctor Emiliano Cruz Hernández.

### Descripción del proyecto: 

Dado un archivo que se busca cifrar y repartir en un número de entidades, este sistema implementa el Esquema de Secreto Compartido de Shamir. 

Dado un archivo, se generaŕa otro archivo cifrado y una contraseña que se dividirá en un número n de partes de forma que si se obtienen k<=n partes de esa llave, puede descifrarse el archivo cifrado, dando la limitación de que con menos de k partes de la contraseña, no se revela información absoluta sobre el archivo original. 

Para más información se puede consultar la carpera de referencias. Una descripción más detallada se brinda en "DescripcionProyecto.pdf" y la publicación sobre el Esquema de secreto compartido se encuentra en "Shamir-HowToShareASecret.pdf".

### Compilación del sistema:

Utilzamos mvn para contruir el proyecto. Se requiere previa intalación del mismo.
Ya intalado, ubicarse dentro de la carpeta Modelado-Proyecto3/proyecto3/ donde se ubica el archivo pom.xml y ejecutar:
```
$ mvn install
```
Esto generará un archivo .jar dentro de /target el cual es el ejecutable del sistema. Un ejemplo para el cifrado y descifrado se brinda más adelante.

### Ejecución:
El programa debe funcionar en dos modalidades, para cifrar (opción c) y para descifrar (opción d).

#### Cifrar:
Se debe proporcionar, en la línea de llamada:
1. La opción -c.
2. La ruta del archivo en el que serán guardadas las n evaluaciones del polinomio.
3. El número de partes (n) en la que se quiere dividir la contraseña.
4. El número mínimo (k) de partes con la cual el archivo puede ser descifrado. (1 < k ≤ n).
5. La ruta del archivo con el documento claro.

Ya con el programa en ejecución se solicitará al usuario una contraseña con la cual se va a cifrar el documento. Esta contraseña no es de vital ofuscación hacia las partes en la que se va a dividir la contraseña segura. El sistema generará una nueva desconocida para cada parte.

Al final se va a generar dos archivos: 
1. Uno de extensión .aes que es el archivo original cifrado
2. Uno de extensión .frg que son las n partes en la cual se dividió la contraseña necesaria para poder descifrar el archivo cifrado.

#### Descifrar:
Se debe proporcionar, en la línea de llamada:
1. La opción -d.
2. El nombre del archivo con, al menos, t de las n evaluaciones del polinomio.
3. El nombre del archivo cifrado.

Si se dan las llaves suficientes y el archivo cifrado correspondiente, se generará un archivo que contiene el contenido original, pero descifrado. 

### Ejemplo de Ejecución:

Para cifrar un docummento:
```
$ java -jar target/Proyecto03.jar -c llaves 5 4 DescripcionProyecto.pdf
```
Para descifrar un documento:

```
$ java -jar target/Proyecto03.jar -d llaves.fgr  DescripcionProyecto.pdf.aes

```






