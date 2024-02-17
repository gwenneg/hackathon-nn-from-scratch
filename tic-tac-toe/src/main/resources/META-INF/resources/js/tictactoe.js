function onDocumentReady(fn) {
    if (document.readyState === "complete" || document.readyState === "interactive") {
        setTimeout(fn, 1);
    } else {
        document.addEventListener("DOMContentLoaded", fn);
    }
}

async function mark(x, y, player) {
    const response= await fetch("/game/mark", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            x: x,
            y: y,
            player: player
        }),
    });
    return response.json();
}

onDocumentReady(function() {

    let next = "cross";

    const boxes = document.querySelectorAll(".grid > div");
    boxes.forEach(box => {
        box.addEventListener("click", () => {
            mark(box.dataset.x, box.dataset.y, 0).then(response => {
                console.log(response);
            });
            if (next === "cross") {
                box.classList.add("cross");
                box.classList.remove("circle");
            } else {
                box.classList.add("circle");
                box.classList.remove("cross");
            }
            next = next === "cross" ? "circle" : "cross";
        });
    });
});
