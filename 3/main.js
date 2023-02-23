const x = () => {
  let seconds = 0;
  const y = () => {
    document.querySelector("h1").textContent = `${seconds++}`;
  };
  return y;
};
const seconds = x();
const int = setInterval(seconds, 1000);
