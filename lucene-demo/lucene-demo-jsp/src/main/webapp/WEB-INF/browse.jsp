<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="lucene" uri="/WEB-INF/lucene.tld"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>browse</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="${contextPath}/style.css" />
    <!-- autocomplete -->
    <link rel="stylesheet" type="text/css" href="${contextPath}/devbridge/style.css" />
    <script type="text/javascript" src="${contextPath}/devbridge/jquery-1.3.2.min.js"></script>
    <script type="text/javascript" src="${contextPath}/devbridge/jquery.autocomplete-min.js"></script>
</head>
<body>
    <div id="body_inside">
      <div id="header_pane">
          <div class="header_form">
              <lucene:searchform />
          </div>
      </div>
      
      <div id="content_pane">
          <lucene:hierarchy hierarchy="${hierarchy}" />
          
          <div class="vertical_pager">
              <lucene:pager pages="${documents.pages}" />
          </div>
          
          <div id="results_pane">
              <div class="results_total">
                  total hits: ${documents.totalHits} (${totaltime})
              </div>
              
              <lucene:documents documents="${documents}" />
              
              <div class="spacer"></div>
              
              <lucene:pager pages="${documents.pages}" />
          </div>
          
          <div class="spacer"></div>
      </div>
    </div>
    
    <script type="text/javascript">
    jQuery(function() {
      var options = { 
        serviceUrl:'${contextPath}/suggest',
        noCache: false, // true on debug
        minChars:3
      };
      $('#text').autocomplete(options);
      $('#text').focus();
    });
    </script>
</body>
</html>
