            var stompClient = null;
            var privateStompClient = null;

            socket = new SockJS('/ws');
            privateStompClient = Stomp.over(socket);
            privateStompClient.connect({}, function(frame) {
            var gameId = document.getElementById('gameId').value;
                    privateStompClient.subscribe('/join/' + gameId, function(result) {
                        var joinDto = JSON.parse(result.body);
                        showAlert(joinDto.name + ' joined the game !');
                        if(joinDto.exception){
                            showException(joinDto.exception);
                        }else{
                            updatePlayers(joinDto.start);
                        }
                    });
                });

            stompClient = Stomp.over(socket);

            function joinGame(){
                removeJoinButton();
                var gameId = document.getElementById('gameId').value;
                stompClient.send("/app/join", {}, JSON.stringify({'gameId':gameId}));
            }

            $(document).ready(function() {
                $("#join-player-alert").hide();
            });

            function showAlert(text){
                document.getElementById('playerJoinName').innerHTML = text;
                $("#join-player-alert").fadeTo(2000, 500).slideUp(500, function() {
                  $("#join-player-alert").slideUp(500);
                });
            };

            function updatePlayers(startGame) {
                var gameId = document.getElementById('gameId').value;
                $.post("/thousand/games/" + gameId + "/room/players").done(function (fragment) {
                    $("#players").replaceWith(fragment);
                });
                if(startGame){
                    loadingGame();
                }
            };

            function loadingGame(){
                removeJoinButton();

                document.getElementById("startGameDiv").style.display = 'block';
                var max = document.getElementById("progressBar").max;
                var timeleft = max;
                var downloadTimer = setInterval(function(){
                  if(timeleft <= 0){
                    clearInterval(downloadTimer);
                    window.location.href = '/thousand/games/'+ document.getElementById('gameId').value;
                  }

                  var nextPbValue =  max - timeleft;
                  document.getElementById("progressBar").value = nextPbValue;
                  timeleft -= 1;
                }, 1000);
            }

            function showException(exceptionMessage) {
                 document.getElementById('errorText').innerHTML = exceptionMessage;
                $("#error-alert").fadeTo(2000, 500).slideUp(500, function() {
                  $("#error-alert").slideUp(500);
                });
            }

            function removeJoinButton(){
                var joinGameButton= document.getElementById('joinGameButton');
                if(joinGameButton!=null) joinGameButton.remove();
            }