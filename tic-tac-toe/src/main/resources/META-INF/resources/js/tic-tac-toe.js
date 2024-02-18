const WS = new WebSocket("ws://localhost:8080/tic-tac-toe");

WS.onmessage = function(message) {
    const payload = JSON.parse(message.data);
    switch (payload.command) {
        case "state":
            updateGrid(payload.state);
            break;
        case "info":
            console.log(payload.info);
            break;
        case "game_over":
            // TODO
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
    for (i = 0; i < state.length; i++) {
        for (j = 0; j < state[i].length; j++) {
            if (state[i][j].length > 0) {
                const SQUARE = document.querySelector('[data-x="' + i + '"][data-y="' + j + '"]');
                SQUARE.classList.add(state[i][j]);
            }
        }
    }
}

onDocumentReady(() => {

    const squares = document.querySelectorAll(".grid > div");

    squares.forEach(square => {
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
    }

    const stopButton = document.getElementById("stop");
    stopButton.onclick = function() {
        send({
            "command": "stop"
        });
        squares.forEach(square => {
            square.classList.remove("disabled", "cross", "circle");
        });
    }

});
