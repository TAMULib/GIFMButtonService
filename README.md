[![Build Status][build-badge]][build-status]
[![Coverage Status][coverage-badge]][coverage-status]

# Get It For Me Button Service

A *Spring* backend for the *Get It For Me (GIFM) Button Service* developed and maintained by [Texas A&M University Libraries][tamu-library].

This is a service for providing links to the [Illiad request management system][illiad-url].
These links are customized based on the individual item in the library catalog.

<details>
<summary>Table of contents</summary>

  - [Deployment](#deployment)
  - [Developer Documentation](#developer-documentation)
  - [Additional Resources](#additional-resources)

</details>


## Deployment

For a quick and easy deployment using `docker-compose` consider using the [GIFM Button App Repo][app-repo].

For _advanced use cases_, or when `docker-compose` is unavailable, the UI may be either started using `docker` directly or even manually started.
This process is further described in the [Deployment Guide][deployment-guide].

<div align="right">(<a href="#readme-top">back to top</a>)</div>


## Developer Documentation

- [Contributors Documentation][contribute-guide]
- [Deployment Documentation][deployment-guide]
- [API Documentation][api-documentation]

<div align="right">(<a href="#readme-top">back to top</a>)</div>


## Additional Resources

Please feel free to file any issues concerning GIFM Button Service to the issues section of the repository.

Any questions concerning GIFM Button Service can be directed to helpdesk@library.tamu.edu.

Copyright © 2022 Texas A&M University Libraries under the [MIT License][license].

<div align="right">(<a href="#readme-top">back to top</a>)</div>


<!-- LINKS -->
[app-repo]: https://github.com/TAMULib/GIFMButton
[build-badge]: https://github.com/TAMULib/GIFMButtonService/workflows/Build/badge.svg
[build-status]: https://github.com/TAMULib/GIFMButtonService/actions?query=workflow%3ABuild
[coverage-badge]: https://coveralls.io/repos/github/TAMULib/GIFMButtonService/badge.svg
[coverage-status]: https://coveralls.io/github/TAMULib/GIFMButtonService

[api-documentation]: https://tamulib.github.io/GIFMButtonService
[tamu-library]: http://library.tamu.edu
[illiad-url]: https://www.atlas-sys.com/illiad/

[deployment-guide]: DEPLOYING.md
[contribute-guide]: CONTRIBUTING.md
[license]: LICENSE
