<div align="center">
    <img width="50%" src="https://github.com/sopra-fs23-group-22/sopra-fs23-group-22-client/blob/main/src/styles/img/Stratego_logo.png" alt="Logo">
</div>

# SoPra FS23 – Stratego
## Table of Contents

- [SoPra FS23 – Stratego](#sopra-fs23--stratego)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Technologies](#technologies)
  - [High-level components](#high-level-components)
  - [Getting started](#getting-started)
    - [Prerequisites and Installation](#prerequisites-and-installation)
    - [Build](#build)
    - [Run](#run)
    - [Test](#test)
    - [Development Mode](#development-mode)
    - [Debugging](#debugging)
    - [Deployment](#deployment)
  - [Roadmap](#roadmap)
  - [Authors](#authors)
  - [License](#license)
  - [Acknowledgements](#acknowledgements)
  - [Links](#links)

## Introduction

Stratego is a classic board game has been enjoyed by many strategy enthusiasts over years. The project aims to create an online version of the game where worldwide players can easily access. Stratego is a 1vs1 game which requires careful planning, deduction, and bluffing, as players attempt to determine the ranks of their opponent’s hidden pieces and make tactical decisions. To start a game, users need to register first. A registered user can start a game by creating a room or joining an available room. After each game, they can choose to play with the same person again or go to lobby to find new challengers. Profile provides a record of the player's wins and losses, allowing players to track their progress and compare achievements.

## Technologies

<img src="https://user-images.githubusercontent.com/91155454/170885686-bd14da8d-5070-49ac-b88d-baa2e20729bf.svg" width="16" height="16" /> [**Gradle**](https://gradle.org/)
                                                                                                 
<img src="https://user-images.githubusercontent.com/91155454/170843203-151000ab-db93-4750-b4f4-ba4060a23d53.png" width="16" height="16" /> [**Java**](https://java.com/)  

<img src="https://user-images.githubusercontent.com/91155454/170842503-3a531289-1afc-4b9c-87c1-cc120d9229ce.svg" style='visibility:hidden;' width="16" height="16" /> [**REST**](https://en.wikipedia.org/wiki/Representational_state_transfer)  

<img src="https://user-images.githubusercontent.com/91155454/170843632-39007803-3026-4e48-bb78-93836a3ea771.png" style='visibility:hidden;' width="16" height="16" /> [**WebSocket**](https://en.wikipedia.org/wiki/WebSocket)

<img src="https://github.com/get-icon/geticon/blob/master/icons/github-icon.svg" width="16" height="16" /> [**GitHub**](https://github.com/)	

<img src="https://sv443.net/cdn/jokeapi/icon_readme.png" width="16" height="16" /> [**Joke API**](https://v2.jokeapi.dev/)

<img src="https://github.com/get-icon/geticon/blob/master/icons/google-cloud-platform.svg" width="16" height="16" /> [**Google Cloud Platform**](https://cloud.google.com/)

<img src="https://user-images.githubusercontent.com/91155454/170843438-4e721d42-5d97-4126-9739-ce049d0d8701.png" style='visibility:hidden;' width="16" height="16" /> [**Spring**](https://spring.io/)  

<img src="https://user-images.githubusercontent.com/91155454/170887616-39d92726-e081-45a4-8192-11f41297c98c.png" style='visibility:hidden;' width="16" height="16" /> [**JUnit**](https://junit.org/)  

<img src="https://user-images.githubusercontent.com/91155454/170843395-534f90bd-793d-477d-8626-4d8015c6041a.png" style='visibility:hidden;' width="16" height="16" /> [**Hibernate**](https://hibernate.org/)

## High-level components

The high-level architecture follows the Repository-Service Pattern. It promotes the separation of concerns with the introduction of three entities: Controller, Service, and Repository. 

// follwing need to be changed:
//According to the above mentioned pattern a lot of work is executed in the [LobbyService](https://github.com/sopra-fs22-group-16/sopra-fs22-group-16-server/blob/readme/src/main/java/ch/uzh/ifi/hase/soprafs22/service/LobbyService.java) and therefore, it depicts a main component. A lobby represents the meeting point for players, the possibility to change the player's name, and the starting point for a game. Whenever all users are set, the game will start. So, the LobbyService can be seen as the interface between the actual game and all necessary administration logic.

Unsurprisingly for a game, another major part of the logic is located in the [GameService](https://github.com/sopra-fs22-group-16/sopra-fs22-group-16-server/blob/readme/src/main/java/ch/uzh/ifi/hase/soprafs22/service/GameService.java). After all user requests are digested by the respective controllers, game related actions are forwarded to the GameService which takes care of the main options for a play unit: attack, move, and surrender. It triggers the underlying models correspondingly, handles and responds to requests by the controller. Besides that, it also triggers the creation of statistics as soon as a game is finished.

And last but not least, the [Game](https://github.com/sopra-fs22-group-16/sopra-fs22-group-16-server/blob/readme/src/main/java/ch/uzh/ifi/hase/soprafs22/game/Game.java) class also bundles multiple functionalities. The Game combines multiple elements to the actual game. It consists of the game map, the game units, the players that control the units, and makes adjustments according to settings like the game mode (casual or ranked) or the game type (1vs1 or - in the future - 2vs2). It also calculates the health points after an attack taking the counter attack into account, and informs about the ending of a game.//


## Getting started
<p>
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.
</p >

### Prerequisites and Installation
Follow the instruction guide for the [Stratego server](https://github.com/sopra-fs23-group-22/sopra-fs23-group-22-server) and [Stratego client](https://github.com/sopra-fs23-group-22/sopra-fs23-group-22-client).

Get the server

```bash
git clone https://github.com/sopra-fs23-group-22/sopra-fs23-group-22-server.git
```
and open the project with an IDE of your choice.

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

### Test

```bash
./gradlew test
```

A test coverage over 87.9% with JUnit 5 was achieved with unit tests.

### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

```bash
./gradlew build --continuous
```

and in the other one:

```bash
./gradlew bootRun
```

### Debugging

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command),
do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Use **Run**/Debug"Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

This launches the test runner in an interactive watch mode.
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### Deployment
After each commit to the master branch, automatic Github Actions get executed which deploy our application to [Google Cloud Platform](https://cloud.google.com/).


## RoadMap
- Add friend function.
User can add others by sending requests and unfriend friends directly.
- Add Chat funtion using external api.
Users can send messages to other users on lobby page or send messages to opponent while playing the game.

## Authors

* **Anqi Xu**  - [anqiXu33](https://github.com/anqiXu33)
* **Chenfei Xiong**  - [rChenfeiXiong](https://github.com/rChenfeiXiong)
* **Jiachen Bao** - [jiachencindybao](https://github.com/jiachencindybao)
* **Shiran Liu** - [ShiranLiu](https://github.com/ShiranLiu)
* **Stefan Plüss** - [stefanpluess](https://github.com/stefanpluess)


## License
This project is licensed under [GPLv3](https://www.gnu.org/licenses/gpl-3.0.en.html), which guarantees end users the freedoms to run, study, share and modify the software.

## Acknowledgements
* This project is based on the [SoPra FS23 - Client Template](https://github.com/HASEL-UZH/sopra-fs23-template-client)
* Thanks to **Hyeongkyun Kim** - [hk-kaden-kim](https://github.com/hk-kaden-kim) who supported us as a TA during this project.

## Links
* [Stratego Client Website](sopra-fs23-group-22-client.oa.r.appspot.com)
* [Stratego Server Website](sopra-fs23-group-22-server.oa.r.appspot.com)
* [SonarCloud](https://sonarcloud.io/organizations/sopra-fs23-group-22/projects)
* [Issue tracker](https://github.com/sopra-fs23-group-22/sopra-fs23-group-22-client/issues)
