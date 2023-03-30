var stompClient = null;
var privateStompClient = null;

socket = new SockJS('/ws');
privateStompClient = Stomp.over(socket);
privateStompClient.connect({}, function (frame) {
    var gameId = document.getElementById('gameId').value;
    privateStompClient.subscribe('/reload-board/' + gameId, function (result) {
        updateBoard();
    });
});

stompClient = Stomp.over(socket);

function updateBoard() {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == XMLHttpRequest.DONE) {
            $("#board").replaceWith(xhr.responseText);
        }
    }
    xhr.open('GET', "/thousand/games/" + document.getElementById("gameId").value + "/board/reload", true);
    xhr.send(null);
}

function takeCards() {
    var xhr = new XMLHttpRequest();
    var url = '/thousand/games/' + document.getElementById("gameId").value + '/take-cards';
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.onreadystatechange = function () { };
    xhr.send();
}

let selectedCard =null;
function giveCardToPlayer(card, playersInfo){
    if(selectedCard!=null && (selectedCard.suit!==card.suit || selectedCard.rank!==card.rank)){
        slideOutCard(selectedCard);
    }
    slideOutCard(card);

    if(selectedCard!=null)
        showTargetPlayerButtons(playersInfo);
    else
        removeGiveCardBtn();

}


function slideOutCard(cardDto) {
    var cardSpan = document.getElementById(cardDto.suit + '_' + cardDto.rank);
    cardSpan.style.marginBottom = selectedCard != null ? '0%' : '3%';
    if (selectedCard != null) selectedCard = null;
    else selectedCard = cardDto;
}

function showTargetPlayerButtons(playersInfo){
    console.log(playersInfo);
    for (const [key, value] of Object.entries(playersInfo)) {
        console.log(value);

        if(value.cardsCount==7)
            addGiveCardBtn(value.name);
    }
}

function removeGiveCardBtn(){
    var throwOneBtnDiv = document.getElementById('targetPlayerDiv');
    throwOneBtnDiv.innerHTML="";
}

function addGiveCardBtn(name){
    var throwOneBtnDiv = document.getElementById('targetPlayerDiv');

    var btnElement = document.createElement('button');
    btnElement.setAttribute('class', 'btn btn-warning');
    btnElement.onclick = function () { sendGiveCardRequest(name) };
    btnElement.style.width='30%';
    btnElement.style.marginLeft='1%';
    btnElement.innerHTML=name;

    throwOneBtnDiv.appendChild(btnElement)
}

function sendGiveCardRequest(name){
       var xhr = new XMLHttpRequest();
        var url = '/thousand/games/' + document.getElementById("gameId").value + '/auction/give-card';
        xhr.open("POST", url, true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.onreadystatechange = function () { };

        var giveCardDto = {"playerName" : name, "card" : selectedCard};
        var data = JSON.stringify(giveCardDto);
        xhr.send(data);

        selectedCard = null
}