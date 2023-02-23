// http://websamuraj.pl/examples/js/projekt8/
// Uzyskaj efekt jak pod tym linkiem
// Użyj setTimeout zamiast setInterval,
// Opóźniej start pisanie każdego słowa
// Uwaga: Zadanie bardzo trudne

const spnText = document.querySelector(".text");
const spnCursor = document.querySelector(".cursor");
const txt = [
  "DZIEŃ DOBRY!!!",
  "DOBRZE, ŻE JESTEŚ",
  "LETS GET IT STARTED ......",
];

// Implementacja
indexTXT = 0;
indexLet = -15;
const addLetter = () => {
  if (indexLet >= 0) {
    spnText.textContent += txt[indexTXT][indexLet];
  }
  indexLet++;
  if (indexLet === txt[indexTXT].length) {
    indexTXT++;
    if (indexTXT === txt.length) return;
    return setTimeout(() => {
      indexLet = 0;
      spnText.textContent = "";
      addLetter();
    }, 2000);
  }
  setTimeout(addLetter, 100);
};

// setTimeout(addLetter, 50);

addLetter(); //pierwsze wywołanie

// Animacja kursora (zostaw bez zmian)
const cursorAnimation = () => {
  spnCursor.classList.toggle("active");
};
setInterval(cursorAnimation, 400);
