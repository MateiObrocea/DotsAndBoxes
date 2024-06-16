# Minor 14 - Dots and Boxes Software Project

Welcome to the final programming project of the Software Systems for module 2 by Minor 14 student group. This file
covers the details for the final submission.

# Project Structure

client: Contains all necessary components for the client side, including both Text User Interfaces (TUIs).
gamelogic: Covers the AI strategy, exceptions, game logic model, and user interface.
helper: Contains the Client and Server protocol.
networking: Includes abstract classes for the server.
server: Encompasses all necessary components for the server side.

# Building and Testing

Relevant and non-trivial classes have associated unit and integration tests located in the "test" directory. To run the
tests, right-click on the "test" directory and select "Run 'All Tests'".

To run the server and client within IntelliJ, click on run in the ClientHumanTUI or ClientAITUI and Server files.

# Usage

Files can be either executed in the IntelliJ IDE, or they can be executed using jar files. Please note,
that the jar files need to be executed from the command line. To do so, open a terminal, navigate to the directory
and execute the following command:

```java -jar <filename>.jar```

The following problem may occur: The cmd may not support colors, so the board will not be displayed correctly. This
happens at least on Windows 10.

## Server

Execute server.jar, enter the desired port, and the server is ready to go.

## Human Client

Execute clienthumantui.jar, enter the server's IP address or localhost, and the port. Follow the prompts from the TUI.

## AI Client

Execute clientaitui.jar, enter the server's IP address or localhost, and the port. Choose a strategy by entering a
number between 1 and 3.

# Commands

## Within ClientHumanTUI

In client TUI:

list: Lists all logged-in users.

queue: Queues you for a game.

help: Displays the list of commands.

exit: Exits the game.

## During games:

Above commands

Enter a number between 1 and 59 to make a move.

hint: Get a hint for your next move.

## Within ClientAITUI

Enter a number between 1 and 3 to change the AI strategy.

Or type 'queue' for being queued for a game.



