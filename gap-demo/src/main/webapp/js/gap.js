function init_gap(it) {
    var method = (it.version == null) ? 'put' : 'post';
    $('#submit').button().html('<span class="ui-button-text">' + method + '</span>');
    set_gap_vals(it);

    $('#form').submit(function(event) {
        event.preventDefault();
        $.ajax({
           type : 'put',
           url : it.link,
           data : $(this).serialize(),
           dataType : 'json'

        }).done(function(data) {
            set_gap_vals(data);
            $('#version').effect('highlight', 1000);
            $('#description').effect('highlight', 1000);

        }).fail(function(error) {
            console.error(error.statusText);
        });
    });
}

function set_gap_vals(data) {
   $('#gapid').val(data.id);
   $('#version').val(data.version);
   $('#description').val(data.description);
}
