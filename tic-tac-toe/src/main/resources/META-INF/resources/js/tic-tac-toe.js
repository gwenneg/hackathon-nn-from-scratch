const WS = new WebSocket("ws://localhost:8080/tic-tac-toe");

WS.onmessage = function(message) {
    const payload = JSON.parse(message.data);
    switch (payload.command) {
        case "state":
            updateGrid(payload.state);
            break;
        case "info":
            document.getElementById("info").textContent = payload.info;
            break;
        case "game_over":
            const SQUARES = document.querySelectorAll(".grid > div");
            SQUARES.forEach(square => {
                square.classList.add("disabled");
            });
            document.getElementById("info").textContent = payload.player + " won!";
            break;
        default:
            console.warn("Unexpected command: " + payload.command);
            break;
    }
}

function send(payload) {
    WS.send(JSON.stringify(payload));
}

function onDocumentReady(fn) {
    if (document.readyState === "complete" || document.readyState === "interactive") {
        setTimeout(fn, 1);
    } else {
        document.addEventListener("DOMContentLoaded", fn);
    }
}

function updateGrid(state) {
    for (x = 0; x < state.length; x++) {
        for (y = 0; y < state[x].length; y++) {
            if (state[x][y].length > 0) {
                const SQUARE = document.querySelector('[data-x="' + x + '"][data-y="' + y + '"]');
                SQUARE.classList.add(state[x][y]);
                SQUARE.classList.add("disabled");
            }
        }
    }
}

onDocumentReady(() => {

    const SQUARES = document.querySelectorAll(".grid > div");

    SQUARES.forEach(square => {
        square.onclick = function() {
            if (!square.classList.contains("disabled")) {
                square.classList.add("disabled");
                send({
                    "command": "mark",
                    "x": Number(square.dataset.x),
                    "y": Number(square.dataset.y)
                });
            }
        }
    });

    const startButton = document.getElementById("start");
    startButton.onclick = function() {
        send({
            "command": "start",
            "playerType1": document.getElementById("playerType1").value,
            "playerType2": document.getElementById("playerType2").value
        });
        SQUARES.forEach(square => {
            square.classList.remove("disabled");
        });
    }

    const stopButton = document.getElementById("stop");
    stopButton.onclick = function() {
        send({
            "command": "stop"
        });
        SQUARES.forEach(square => {
            square.classList.add("disabled");
            square.classList.remove("cross", "circle");
        });
        document.getElementById("info").innerText = "";
    }

});
