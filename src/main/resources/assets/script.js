$(function () {
  load();
});

function create(name, url) {
  if(url.includes("http://") || url.includes("https://")){
    $.post("/service", JSON.stringify({name: name, url: url}), function () {
      load();
    }, "json");
  } else {
    alert("The url needs to contain a full address\nExample: https://www.google.se");
  }

}

function remove(id) {
  $.ajax({
    method: "DELETE",
    url: "/service/" + id
  }).done(function () {
    load();
  });
}

function load() {
  console.log("Loading page");
  $("#content").children().remove();
  $.getJSON("/service", function (data) {
    $.each(data, function (k, v) {
      if (k === "services") {
        $.each(v, function(key, val) {
          $("<tr><td>" + val.id + "</td><td>" + val.name + "</td><td>" + val.url + "</td>" +
          "<td>" + val.status + "</td>" +
          "<td>" +
          "<button class='service-delete' " +
          "data-name='" + val.name + "' " +
          "data-url='" + val.url + "' " +
          "data-id='" + val.id + "' " +
          "data-status='" + val.status + "'>Remove</button>" +
          "</td>" +
          "</tr>").appendTo("#content");
        })
      }
    })
  });
  initCallbacks();
}

function initCallbacks() {
  $("#content").unbind().on("click", ".service-delete", function() {
    var id = $(this).data("id");
    console.log("Pressing Remove button: "+id);
    remove(id);
  });

  $("#add-btn").unbind().click(function() {
    var name = $("#input-name").val();
    var url = $("#input-url").val();
    $("#input-name").val('');
    $("#input-url").val('');
    console.log("Pressing add button: "+name+", "+url);
    create(name, url);
  });
}
