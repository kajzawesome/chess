# Shortcuts for running chess in Docker. Type `make` to see them all.
COMPOSE = docker compose

.DEFAULT_GOAL := help
.PHONY: help up down restart rebuild client logs ps reset

help:
	@echo "Chess Docker commands:"
	@echo "  make up       Start the database and server (http://localhost:8080)"
	@echo "  make down     Stop everything (saved games are kept)"
	@echo "  make client   Open a chess client in this terminal (run again in another terminal for player 2)"
	@echo "  make logs     Follow the server's log output"
	@echo "  make ps       Show what's running"
	@echo "  make restart  Stop and start again"
	@echo "  make rebuild  Rebuild the app after code changes, then start"
	@echo "  make reset    Stop everything and DELETE all saved users and games"

up:
	$(COMPOSE) up -d --build --wait db server
	@echo "Chess server is running at http://localhost:8080 - type 'make client' to play"

down:
	$(COMPOSE) --profile client down

restart: down up

rebuild:
	$(COMPOSE) build --no-cache server
	$(COMPOSE) up -d --wait db server

client:
	$(COMPOSE) run --rm client

logs:
	$(COMPOSE) logs -f server

ps:
	$(COMPOSE) ps

reset:
	$(COMPOSE) --profile client down -v
