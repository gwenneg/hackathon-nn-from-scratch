const WS = new WebSocket("ws://localhost:8080/tic-tac-toe");

WS.onmessage = function(message) {
    const payload = JSON.parse(message.data);
    switch (payload.command) {
        case "state":
            updateGrid(payload.state);
            break;
        case "info":
            console.error(payload.info);
            break;
        case "game_over":
            const SQUARES = document.querySelectorAll(".grid > div");
            SQUARES.forEach(square => {
                square.classList.add("disabled");
            });
            if (payload.player === "Player1") {
                document.getElementById("player1-status").textContent = "WINNER";
            } else if (payload.player === "Player2") {
                document.getElementById("player2-status").textContent = "WINNER";
            } else {
                document.getElementById("player1-status").textContent = "DRAW";
                document.getElementById("player2-status").textContent = "DRAW";
            }
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
            square.classList.remove("cross", "circle", "disabled");
        });
        document.getElementById("player1-status").innerText = "";
        document.getElementById("player2-status").innerText = "";
    }

    const exportButton = document.getElementById("export");
    exportButton.onclick = function() {
        send({
            "command": "export"
        });
    }

});
