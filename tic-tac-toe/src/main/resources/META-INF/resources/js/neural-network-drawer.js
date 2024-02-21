function drawNeuralNetwork(parentElement, elementId, color, ...networkDef) {

    const LAYER_SPACING = 125;
    const NEURON_RADIUS = 8;
    const NEURON_STROKE_WIDTH = 2.5;
    const NEURON_SPACING = 1;
    const SYNAPSE_STROKE_WIDTH = 0.2;

    let div = document.createElement("div");
    div.setAttribute("id", elementId);
    div.style.position = "relative";
    parentElement.append(div);

    let networkBackground = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    div.append(networkBackground);

    let layers = [];
    for (i = 0; i < networkDef.length; i++) {
        let layer = [];
        let top = (Math.max(...networkDef) - networkDef[i]) / 2;
        for (j = 0; j < networkDef[i]; j++) {
            layer.push({
                cx: NEURON_RADIUS + NEURON_STROKE_WIDTH + i * LAYER_SPACING,
                cy: (top + j) * (NEURON_RADIUS * 2 + NEURON_STROKE_WIDTH * 2 + NEURON_SPACING) + NEURON_RADIUS + NEURON_STROKE_WIDTH
            });
        }
        layers.push(layer);
    }

    for (i = 0; i < layers.length; i++) {
        for (j = 0; j < layers[i].length; j++) {

            let neuron = document.createElementNS("http://www.w3.org/2000/svg", "circle");
            neuron.setAttribute("cx", layers[i][j].cx);
            neuron.setAttribute("cy", layers[i][j].cy);
            neuron.setAttribute("r", NEURON_RADIUS);
            neuron.setAttribute("stroke", color);
            neuron.setAttribute("stroke-width", NEURON_STROKE_WIDTH);
            neuron.setAttribute("fill", "none");
            document.querySelector("#" + elementId + " > svg").append(neuron);

            let activation = document.createElementNS("http://www.w3.org/2000/svg", "svg");
            activation.setAttribute("width", NEURON_RADIUS * 2);
            activation.setAttribute("height", NEURON_RADIUS * 2);
            activation.setAttribute("data-layer", i);
            activation.setAttribute("data-neuron", j);
            activation.classList.add("activation");
            activation.style.position = "absolute";
            activation.style.top = layers[i][j].cy - NEURON_RADIUS;
            activation.style.left = layers[i][j].cx - NEURON_RADIUS;
            div.append(activation);

            let circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
            circle.setAttribute("cx", NEURON_RADIUS);
            circle.setAttribute("cy", NEURON_RADIUS);
            circle.setAttribute("r", NEURON_RADIUS);
            circle.setAttribute("fill", color);
            activation.append(circle);

            if (i < layers.length -1) {
                for (k = 0; k < layers[i + 1].length; k++) {
                    let synapse = document.createElementNS("http://www.w3.org/2000/svg", "line");
                    synapse.setAttribute("x1", layers[i][j].cx + NEURON_RADIUS + NEURON_STROKE_WIDTH) ;
                    synapse.setAttribute("x2", layers[i + 1][k].cx - NEURON_RADIUS - NEURON_STROKE_WIDTH);
                    synapse.setAttribute("y1", layers[i][j].cy);
                    synapse.setAttribute("y2", layers[i + 1][k].cy);
                    synapse.setAttribute("stroke", color);
                    synapse.setAttribute("stroke-width", SYNAPSE_STROKE_WIDTH);
                    document.querySelector("#" + elementId + " > svg").append(synapse);
                }
            }
        }
    }

    networkBackground.setAttribute("width", (networkDef.length - 1) * LAYER_SPACING + (NEURON_RADIUS + NEURON_STROKE_WIDTH) * 2);
    networkBackground.setAttribute("height", Math.max(...networkDef) * (NEURON_RADIUS + NEURON_STROKE_WIDTH) * 2 + (Math.max(...networkDef) - 1) * NEURON_SPACING);
}
