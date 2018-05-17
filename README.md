## FireLottoStats
FireLotto - an blockchain lottery analytical statistics tool

## Build requirements
- Java 8
- Sbt 0.13.16
- PostgreSQL 9.x

## Configuration
- Copy somwhere `src/main/configs/config_template.json`
- Setup Database connection
- Change `statsOutDir` path variable to let program collect all output statistics
- Change `walletsOutDir` path variable to let program know where to find wallets information (passwords is faked)
- Look at `src/main/build.sbt` and setup Database connection
- Register at `myetherapi.com` and get your ApiKey, then place them at your `config.json` > `web3Provider` > `apiKey`

## Code generation
run `sbt gen-tables` to generate Slick database model

## Run
- make sure that you defined full path to application config like `firelotto-stats.jar path/to/config/config.json` template of config placed at `src/main/configs/config_template.json`

## Known issues
- impossible to improve speed of collection information because we can't use local Ethereum node [here is explanation](https://github.com/ethereum/go-ethereum/issues/16309) and need to use external Ethereum node, there are implemented load balancer and we have big chance to be blocked