const prefix = random(1000, 9999) + '.';

export function createId() {
  return uniqId(prefix);
}

function random(min, max, floating) {
    if (max == null) {
        max = min;
        min = 0;
    }

    const rand = Math.random();

    if (floating || min % 1 || max % 1) {
        return Math.min(
            min +
                rand *
                    (max - min + parseFloat('1e-' + ((rand + '').length - 1))),
            max
        );
    }

    return min + Math.floor(rand * (max - min + 1));
};

let idCounter = 0;
function uniqId(prefix) {
    const id = ++idCounter + '';

    return prefix ? prefix + id : id;
};
