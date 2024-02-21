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
        case "activations":
            updateActivations(payload.activations);
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
            if (demoMode) {
                sleep(200).then(() => {
                    const SQUARES = document.querySelectorAll(".grid > div");
                    start(SQUARES);
                });
            }
            break;
        default:
            console.warn("Unexpected command: " + payload.command);
            break;
    }
}

async function sleep(ms) {
    await new Promise(r => setTimeout(r, ms));
}

function send(payload) {
    WS.send(JSON.stringify(payload));
}

function updateActivations(activations) {
    for (i = 0; i < 9; i++) {
        let neuron = document.querySelector('[data-layer="0"][data-neuron="' + i + '"]');
        neuron.style.opacity = 1;
    }
    for (i = 0; i < activations.length; i++) {
        let max = 0;
        for (j = 0; j < activations[i].length; j++) {
            if (activations[i][j] > max) {
                max = activations[i][j];
            }
        }
        for (j = 0; j < activations[i].length; j++) {
            let neuron = document.querySelector('[data-layer="' + (i + 1) + '"][data-neuron="' + j + '"]');
            neuron.style.opacity = activations[i][j] / max;
        }
    }
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

function start(SQUARES) {
    send({
        "command": "start",
        "playerType1": document.getElementById("playerType1").value,
        "playerType2": document.getElementById("playerType2").value,
        "demoMode": demoMode
    });
    SQUARES.forEach(square => {
        square.classList.remove("cross", "circle", "disabled");
    });
    document.getElementById("player1-status").innerText = "";
    document.getElementById("player2-status").innerText = "";
    document.querySelectorAll(".activation").forEach(neuron => neuron.style.opacity = 0);
}

let demoMode = false;

onDocumentReady(() => {

    let body = document.getElementById("game");
    let networkDef = [9, 32, 32, 32, 32, 9];
    drawNeuralNetwork(body, "network", "#4dabf7", ...networkDef);

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
        start(SQUARES);
    }

    const demoButton = document.getElementById("demo");
    demoButton.onclick = function() {
        demoMode = !demoMode;
        if (demoMode) {
            document.getElementById("playerType1").value = "DUMB_BOT";
            document.getElementById("playerType2").value = "NEURAL_NETWORK";
            start(SQUARES);
        }
    }

    const exportButton = document.getElementById("export");
    exportButton.onclick = function() {
        send({
            "command": "export"
        });
    }

});
