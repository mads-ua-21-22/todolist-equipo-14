# ToDoList (Práctica 2 MADS)

Repositorio de GitHub:
- (https://github.com/mads-ua-21-22/mads-todolist-CarlosPC94)

Repositorio de Docker: 
- (https://hub.docker.com/repository/docker/cpc73/mads-todolist)

Tablero de trello: 
- (https://trello.com/b/9DIGruu4/todolist-mads)


## Listado de nuevas clases y métodos implementados.

### 1. Página "Acerca de":

Se trata de una página html muy básica donde se informa brevemente de la versión de la aplicación, su fecha de release y el desarrollador.


![img.png](img.png)

En la parte superior se encuentra la barra de menú, la cual se explicará detalladamente más adelante.

Código HTML:

~~~
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head th:replace="fragments :: head (titulo='Acerca de')"></head>

<body>
<div th:if="${aux == 0}">
  <nav th:replace="fragments :: Menu (${usuario.nombre}, @{/usuarios/{id}/tareas(id=${usuario.id})}, ${usuario.getAdminApproved()})" ></nav>
</div>
<div th:if="${aux == 1}">
  <nav th:replace="fragments :: Menu (${null}, @{/login}, ${null})" ></nav>
</div>
<div class="container-fluid">
  <div class="container-fluid">
    <h1>ToDoList</h1>
    <ul>
      <li>Desarrollada por Carlos Poveda Cañizares </li>
      <li>Versión 1.0.1 (SNAPSHOT)</li>
      <li>Fecha de release: 12/10/21</li>
    </ul>
  </div>

</div>

<div th:replace="fragments::javascript"/>

</body>
</html>
~~~

Como se puede apreciar, es un codigo muy básico, exceptuando la parte de la barra de menú que explicaré con más detalle en el siguiente apartado.

Por último, su controlador está implementado en "HomeCotroller.java":

~~~
@GetMapping("/about")
    public String about(Model model, HttpSession session) {
        Long idUsuario = managerUserSession.usuarioLogeado(session);
        Usuario usuario = null;

        if(idUsuario != null) {
            managerUserSession.comprobarUsuarioLogeado(session, idUsuario);
            usuario = usuarioService.findById(idUsuario);

            model.addAttribute("usuario", usuario);
            model.addAttribute("aux", 0);
            return  "about";

        }
        else {
            model.addAttribute("usuario", usuario);
            model.addAttribute("aux", 1);
            return "about";
        }
    }
~~~

Como podemos ver en el código, lo primero que realizamos es comprobar si hay una sesión inciada, en caso de que sea así devolverá el usuario en cuestión y un auxiliar=0 que utilizaremos como condicion (th:if), o en caso contrario, devolverá un usuario vacío y el auxiliar=1, en ambos cosas nos retornará a la página "about.html".



