function onDocumentReady(fn) {
    if (document.readyState === "complete" || document.readyState === "interactive") {
        setTimeout(fn, 1);
    } else {
        document.addEventListener("DOMContentLoaded", fn);
    }
}

async function mark(x, y, player) {
    const response = await fetch("/game/mark", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            x: x,
            y: y,
            player: player
        })
    });
    return response.json();
}

async function reset() {
    const response = await fetch("/game/reset", {
        method: "PUT"
    });
}

onDocumentReady(() => {

    reset().then(response => {
        console.log("reset done");
    });

    const squares = document.querySelectorAll(".grid > div");
    squares.forEach(square => {
        square.addEventListener("click", () => {
            if (!square.classList.contains("disabled")) {
                square.classList.add("disabled");
                mark(square.dataset.x, square.dataset.y, -1).then(response => {
                    square.classList.add(response.mark);
                });
            }
        });
    });

});
