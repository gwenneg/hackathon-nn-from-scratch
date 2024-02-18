function onDocumentReady(fn) {
    if (document.readyState === "complete" || document.readyState === "interactive") {
        setTimeout(fn, 1);
    } else {
        document.addEventListener("DOMContentLoaded", fn);
    }
}

async function mark(x, y) {
    const response = await fetch("/game/mark", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ x: x, y: y })
    });
    return response.json();
}

async function reset() {
    return await fetch("/game/reset", {
        method: "PUT"
    });
}

onDocumentReady(() => {

    const squares = document.querySelectorAll(".grid > div");

    squares.forEach(square => {
        square.addEventListener("click", () => {
            if (!square.classList.contains("disabled")) {
                square.classList.add("disabled");
                mark(square.dataset.x, square.dataset.y).then(response => {
                    square.classList.add(response.mark);
                    console.log(response);
                });
            }
        });
    });

    const resetButton = document.getElementById("reset");
    resetButton.addEventListener("click", () => {
       reset().then(response => {
           squares.forEach(square => {
               square.classList.remove("disabled", "cross", "circle");
           });
       });
    });

});
