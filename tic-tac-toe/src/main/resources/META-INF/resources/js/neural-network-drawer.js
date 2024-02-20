function drawNeuralNetworkBackground(parentTag, networkId, color, ...networkDef) {

    const LAYER_SPACING = 200;
    const NEURON_RADIUS = 15;
    const NEURON_STROKE_WIDTH = 4;
    const NEURON_SPACING = 1;
    const SYNAPSE_STROKE_WIDTH = 0.5;

    let svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute("id", networkId);
    parentTag.append(svg);

    let network = [];
    for (i = 0; i < networkDef.length; i++) {
        let layer = [];
        let top = (Math.max(...networkDef) - networkDef[i]) / 2;
        for (j = 0; j < networkDef[i]; j++) {
            layer.push({
                cx: NEURON_RADIUS + NEURON_STROKE_WIDTH + i * LAYER_SPACING,
                cy: (top + j) * (NEURON_RADIUS * 2 + NEURON_STROKE_WIDTH * 2 + NEURON_SPACING) + NEURON_RADIUS + NEURON_STROKE_WIDTH
            });
        }
        network.push(layer);
    }

    for (i = 0; i < network.length; i++) {
        for (j = 0; j < network[i].length; j++) {

            let neuron = document.createElementNS("http://www.w3.org/2000/svg", "circle");
            neuron.setAttribute("cx", network[i][j].cx);
            neuron.setAttribute("cy", network[i][j].cy);
            neuron.setAttribute("r", NEURON_RADIUS)
            neuron.setAttribute("stroke", color);
            neuron.setAttribute("stroke-width", NEURON_STROKE_WIDTH);
            neuron.setAttribute("fill", "none");
            document.getElementById(networkId).append(neuron);

            if (i < network.length -1) {
                for (k = 0; k < network[i + 1].length; k++) {
                    let synapse = document.createElementNS("http://www.w3.org/2000/svg", "line");
                    synapse.setAttribute("x1", network[i][j].cx + NEURON_RADIUS + NEURON_STROKE_WIDTH) ;
                    synapse.setAttribute("x2", network[i + 1][k].cx - NEURON_RADIUS - NEURON_STROKE_WIDTH);
                    synapse.setAttribute("y1", network[i][j].cy);
                    synapse.setAttribute("y2", network[i + 1][k].cy);
                    synapse.setAttribute("stroke", color);
                    synapse.setAttribute("stroke-width", SYNAPSE_STROKE_WIDTH);
                    document.getElementById(networkId).append(synapse);
                }
            }
        }
    }

    svg.setAttribute("width", (networkDef.length - 1) * LAYER_SPACING + (NEURON_RADIUS + NEURON_STROKE_WIDTH) * 2);
    svg.setAttribute("height", Math.max(...networkDef) * (NEURON_RADIUS + NEURON_STROKE_WIDTH) * 2 + (Math.max(...networkDef) - 1) * NEURON_SPACING);
}
