main is ${mainClassName} from app

[app] <% libs.each{ lib -> %> 
  load \${app.home}/lib/${lib.name} <%} %>

