function scoreProject() {
  $('#projectScoring').modal();
  $('#projectScoring').on('hidden.bs.modal', function (e) {
    document.game.submit();
  })
}

function handleTimerExpiry() {
  selectedProjectRole = null;
  $(PROJECT_ROLE_ROW).removeClass("project_role_selected_row");
  scoreProject();
}

var clock = $('.counter').FlipClock(20, {
    clockFace: 'Counter'
  });

  setTimeout(function() {
    var interval = setInterval(function() {
      clock.decrement();
      if(clock.getTime().time <= 0) {
        handleTimerExpiry();
      }     
    }, 1000);
  });
