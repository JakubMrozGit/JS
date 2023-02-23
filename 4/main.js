const btn1 = document.querySelector(".main");
const btn2 = document.querySelector(".reset");
let time = 0;
let active = false;
let int;
const start = () => {
  if (btn1.textContent == "Start") {
    const mili = () => {
      active = !active;
      time += 0.01;
      document.querySelector(".time div").textContent = time.toFixed(2);
    };
    int = setInterval(mili, 10);
    btn1.textContent = "Pauza";
  } else {
    active = !active;
    clearInterval(int);
    btn1.textContent = "Start";
  }
};

const reset = () => {
  document.querySelector(".time div").textContent = "---";
  time = 0;
  active = !active;
};
btn1.addEventListener("click", start);
btn2.addEventListener("click", reset);
