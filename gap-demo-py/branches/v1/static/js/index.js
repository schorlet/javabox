$(document).ready(function() {
   $('a[class=delete]').click(function(event) {
       event.preventDefault();
       var url = $(this).attr('href');
       $.ajax({
          type : 'delete',
          url : url
       });
   });
});
