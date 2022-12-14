# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),

## [Unreleased]

### Changed
- Moved helm charts from `deployment/helm` to `charts`

## [2.0.0] - 2022-11-09
- Created new helm charts for DAPS registration service

### Added
- Registration in DAPS
- Adding BPN number in DAT token
- Provide CRUD operations to EDC connnector registration to the DAPS
- Keycloak protection is added

### Changed
- Generate the controller from the openAPI description
- Update all the used libraries to the latest version
- All smells from SonarQube were fixed

### Removed
- Controller has been removed

### Known knowns
- Cross side scripting (XSS) shall be mitigated (low risk)
- Improving the validation of the input parameters (low risk)
