$( document ).ready(function() {
  console.log( "document loaded" );

  $("a").on("click", function () {
    console.log("a clicked: " + $(this).attr('href'));
    var audio = $("#audio-controls");

    $("#mp3-src").attr("src", $(this).attr('href'));
    audio[0].pause();
    audio[0].load();
    audio[0].oncanplaythrough = audio[0].play();

    return false;
  });
});

$( window ).on( "load", function() {
  console.log( "window loaded" );
});
