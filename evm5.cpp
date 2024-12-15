#include <dos.h>
#include <conio.h>


int currentColor = 0;
int shouldExit = 0;


void drawWindow() {
    textbackground(currentColor);
    clrscr();
    gotoxy(10, 5);
    cprintf("\nPress F4 to change color");
}


void interrupt newKeyboardISR(...) {
    unsigned char scanCode;

    scanCode = inp(0x60);

    if (scanCode == 0x3E) {
        currentColor = (currentColor % 8) + 1;
        drawWindow();
    }
    if (scanCode == 0x01) {
        shouldExit = 1;
    }
    outp(0x20, 0x20);
}

int main() {

    void interrupt (*oldKeyboardISR)(...);
    oldKeyboardISR = getvect(0x09);


    setvect(0x09, newKeyboardISR);
    drawWindow();

    while (!shouldExit) {

    }
    setvect(0x09, oldKeyboardISR);

    return 0;
}
