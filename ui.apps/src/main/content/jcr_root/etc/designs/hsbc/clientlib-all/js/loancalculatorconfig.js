var editor; // use a global for the submit and return data rendering in the examples

$(document).ready(function () {
    editor = new $.fn.dataTable.Editor({
        ajax: {url:"/bin/servlet/loancalculatorconfig",type:'POST',data: {pagePath: document.location.pathname.split(".html")[0]}},
        //ajax: {url:"/bin/servlet/loancalculatorconfig",type:'POST',data: function(d){$('form').serialize();}},
        table: "#example",
        fields: [{
            label: "Configuration Name",
            name: "configName"
        }, {
            label: "Show Currency Input",
            name: "property_showCurrencyInput",
            type:  "radio",
            options: [
                { label: "Yes", value: true },
                { label: "No",  value: false }
            ],
            def: false
        }, {

            /*label: "Images:",
            name: "files[].id",
            type: "uploadMany",
            display: function (fileId, counter) {
                return '<img src="' + editor.file('files', fileId).web_path + '"/>';
            },
            noFileText: 'No images'

            //{"data":[],"files":{"files":{"2":{"id":"2","filename":"20451664_450396218675220_3251083057260900995_o.jpg","filesize":"260947",
            "web_path":"\/upload\/2.jpg","system_path":"\/home\/datat\/public_html\/editor\/upload\/2.jpg"}}},"upload":{"id":"2"}}
            */


            label: "CSV File",
            name: "csvFileUpload",
            type: "upload",
            display: function (fileId, counter) {
                return '<img src="' + editor.file('files', fileId).web_path + '"/>';
            },
            clearText: "Clear",
            noImageText: 'No File'

        }
        ]
    });

    editor.on("preSubmit", function(e, data, action){
        debugger;
        for(var eachVal in data.data[0]){data[eachVal]=data.data[0][eachVal];}
        delete data.data in data
        debugger;
    });
    var table = $('#example').DataTable({
        dom: "Bfrtip",
        ajax: {url:"/bin/servlet/loancalculatorconfig",type:'GET',data: {pagePath: document.location.pathname.split(".html")[0]}},
        columns: [
            /*{data: "users.first_name"},
            {data: "users.last_name"},
            {data: "users.phone"},
            {data: "sites.name"},
            */
            {data: "configName"},
            {data: "property_showCurrencyInput"},
            {data: "csvFileUpload",
                render: function ( file_id ) {
                    return file_id ?
                        '<img src="'+editor.file( 'files', file_id ).web_path+'"/>' :
                        null;
                },
                defaultContent: "No Uploaded CSV File",
                title: "CSV File"
            }
        ],
        select: true,
        buttons: [
            {extend: "create", editor: editor},
            {extend: "edit", editor: editor},
            {extend: "remove", editor: editor}
        ]

    });
});