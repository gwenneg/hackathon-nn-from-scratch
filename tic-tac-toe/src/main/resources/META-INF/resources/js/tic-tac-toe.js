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
            grid.forEach(row => {
                row.forEach(square => square.classList.add("disabled"));
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
                    start();
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

function updateActivations(values) {
    for (i = 0; i < 9; i++) {
        activateNeuron(0, i, 1);
    }
    for (i = 0; i < values.length; i++) {
        let maxValue = Math.max(...values[i]);
        for (j = 0; j < values[i].length; j++) {
            activateNeuron(i + 1, j, values[i][j] / maxValue);
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
                grid[x][y].classList.add(state[x][y]);
                grid[x][y].classList.add("disabled");
            }
        }
    }
}

function start() {
    send({
        "command": "start",
        "playerType1": document.getElementById("playerType1").value,
        "playerType2": document.getElementById("playerType2").value,
        "demoMode": demoMode
    });
    grid.forEach(row => {
        row.forEach(square => square.classList.remove("cross", "circle", "disabled"));
    });
    document.getElementById("player1-status").innerText = "";
    document.getElementById("player2-status").innerText = "";
    for (i = 0; i < 9; i++) {
        activateNeuron(0, i, 0);
    }
}

let demoMode = false;

let grid = [[], [], []];

function initGrid() {
    let squares = document.querySelectorAll(".grid > div");
    squares.forEach(square => {
        let x = Number(square.dataset.x);
        let y = Number(square.dataset.y);
        grid[x][y] = square;
        square.onclick = function() {
            if (!this.classList.contains("disabled")) {
                this.classList.add("disabled");
                send({
                    "command": "mark",
                    "x": x,
                    "y": y
                });
            }
        }
    });
}

onDocumentReady(() => {

    let game = document.getElementById("game");
    let networkDef = [9, 32, 32, 32, 32, 9];
    drawNeuralNetwork(game, "network", "#4dabf7", ...networkDef);

    initGrid();

    const startButton = document.getElementById("start");
    startButton.onclick = function() {
        start();
    }

    const demoButton = document.getElementById("demo");
    demoButton.onclick = function() {
        demoMode = !demoMode;
        if (demoMode) {
            document.getElementById("playerType1").value = "DUMB_BOT";
            document.getElementById("playerType2").value = "NEURAL_NETWORK";
            start();
        }
    }

    const exportButton = document.getElementById("export");
    exportButton.onclick = function() {
        send({
            "command": "export"
        });
    }

});
