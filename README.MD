# Demo Spring Boot CQRS Bank

A production-ready, reactive banking service demonstrating **Event Sourcing** and **CQRS** (Command Query Responsibility Segregation) architecture patterns using Spring Boot 3.5, WebFlux, R2DBC, and RabbitMQ. This reference implementation showcases clean separation of write/read concerns, non-blocking reactive I/O, event-driven architecture, and comprehensive testability using Testcontainers.

## 🎯 Project Overview

This application simulates core banking operations (account creation, deposits, withdrawals, and transfers) while implementing:
- **Command Side**: Processes write operations, validates business rules, persists state, and publishes domain events
- **Query Side**: Handles read-optimized queries for account details, balances, and transaction history
- **Event Store**: Immutable audit log of all domain events for event sourcing and compliance
- **Async Messaging**: Decoupled event publishing via RabbitMQ for scalability and resilience

## 📋 Table of Contents
- [Architecture](#-architecture)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [Prerequisites](#-prerequisites)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Running Tests](#-running-tests)
- [API Documentation](#-api-documentation)
- [Configuration](#-configuration)
- [Observability & Monitoring](#-observability--monitoring)
- [Resilience Patterns](#-resilience-patterns)
- [Development Guidelines](#-development-guidelines)
- [License](#-license)

## 🏗 Architecture

### CQRS Pattern
