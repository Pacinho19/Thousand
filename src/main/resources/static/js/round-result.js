var stompClient = null;
var privateStompClient = null;

socket = new SockJS('/ws');
privateStompClient = Stomp.over(socket);
privateStompClient.connect({}, function (frame) {
    var gameId = document.getElementById('gameId').value;
    privateStompClient.subscribe('/player-ready/' + gameId, function (result) {
        checkReadyResponse(result.body, gameId);
    });
});
stompClient = Stomp.over(socket);

function checkReadyResponse(result, gameId){
    const obj = JSON.parse(result);
    if(obj.nextRound){
        window.location.href = '/thousand/games/' + gameId;
        return;
    }

    updateTable(gameId);
    showPlayerReadyAlert(result.message);
}

function updateTable(gameId){
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == XMLHttpRequest.DONE) {
            $("#roundResultTable").replaceWith(xhr.responseText);
        }
    }
    xhr.open('GET', "/thousand/games/" + gameId + "/round/summary/reload", true);
    xhr.send(null);
}

function showPlayerReadyAlert(message) {
    document.getElementById('playerReadyName').innerHTML = message;
    $("#player-ready-alert").fadeTo(2000, 500).slideUp(500, function() {
        $("#player-ready-alert").slideUp(500);
    });
}