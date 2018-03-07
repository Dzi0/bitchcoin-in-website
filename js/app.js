$( document ).ready(function() {
  console.log( "document loaded" );

  $("a").on("click", function () {
    console.log("a clicked: " + $(this).attr('href'));
    $("#mp3-src").attr("src", $(this).attr('href'));
    return false;
  });
});

$( window ).on( "load", function() {
  console.log( "window loaded" );
});
