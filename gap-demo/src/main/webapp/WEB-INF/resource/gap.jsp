<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" session="false"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>gap</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link type="text/css" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/redmond/jquery-ui.css"
   rel="stylesheet" />
<link type="text/css" href="/css/gap.css" rel="stylesheet" />
</head>
<body>
   <div class="width6 center ui-state-focus">

      <form id="form">
         <div class="ui-widget-header" style="height: 22px; padding: 2px">Edit Gap</div>

         <div class="ui-widget" class="width10" style="padding: 2px">
            <label for="gapid">id</label>
            <input type="text" id="gapid" class="form center" required="required" disabled="disabled" value="${it.id}" />

            <label for="version">version</label>
            <input type="text" id="version" name="version" class="form center" pattern="\d\.\d\.\d" required="required" value="${it.version}" />

            <label for="description">description</label>
            <input type="text" id="description" name="description" class="form center" value="${it.description}" />
         </div>

         <br />
         <div class="ui-widget-footer" style="padding: 2px">
            <button id="submit" class="ui-button" />
         </div>

      </form>
   </div>

   <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.js"></script>
   <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js"></script>
   <script type="text/javascript" src="/js/gap.js"></script>
   <script type="text/javascript">
				$(document).ready(function() {
	            init_gap({
	               id : '${it.id}',
	               version : '${it.version}',
	               description : '${it.description}',
	               link : '${it.link}'
	            });
            });
			</script>
</body>
</html>
