# OfflineStore

A Paper 26.1.2 plugin designed for [minecraftoffline.net](https://www.minecraftoffline.net).

A token-powered shop that lets players spend SimpleVote tokens on cosmetic and server-personalisation items. Players can buy name colours applied via ChromaTag, extra health hearts via SimpleLifesteal, and custom second-line MOTD messages that appear on the server list. MOTDs are purchased for a fixed duration and randomly rotated on the server list until they expire.

## Dependencies

- **Required** [SimpleVote](https://github.com/Jelly-Pudding/SimpleVote) for the token economy
- **Required** [ChromaTag](https://github.com/Jelly-Pudding/ChromaTag) for name colour purchasing and setting
- **Required** [SimpleLifesteal](https://github.com/Jelly-Pudding/SimpleLifesteal) for heart purchasing

## How it works

1. Players earn tokens through SimpleVote and spend them in `/shop` across three categories: **colour**, **heart**, and **motd**.
2. Colours are purchased once and owned permanently. Players can switch between any colours they own at any time with `/shop colour set <colour>`.
3. Hearts cost a flat token amount per heart. The purchase calls the SimpleLifesteal API directly and refunds tokens if the operation fails.
4. MOTD messages are purchased for a set duration (day, week, or month). The server list second line is drawn randomly from all active purchased MOTDs, falling back to the default slogan when none are active.
5. Expired MOTDs are cleaned up automatically on a background timer that runs every hour.

## Commands

| Command | Description |
|---|---|
| `/shop colour list` | List all colours with prices and ownership status |
| `/shop colour buy <colour>` | Purchase a colour |
| `/shop colour set <colour>` | Apply an owned colour to your display name |
| `/shop colour reset` | Reset your display name colour to default |
| `/shop heart info` | Show your current heart count |
| `/shop heart buy` | Purchase one heart |
| `/shop motd list` | List MOTD durations and their prices |
| `/shop motd buy <duration> <message>` | Preview a custom MOTD message |
| `/shop motd confirm <duration>` | Confirm and purchase the previewed message |
| `/shop motd my` | Show your currently active MOTD message |
| `/rules` | Display the server rules |
| `/help [page]` | Display server help across three pages |
| `/kill` | Kill yourself |
| `/donate` | Show the token store link |
| `/plugins` | Show server plugin information |

All commands require the `offlinestore.use` permission (default: `true`).

## Key config settings

| Setting | Description |
|---|---|
| `colour_costs.<colour>` | Token cost for each named colour; `0` means free |
| `heart_cost` | Tokens per purchased heart (default `5`) |
| `motd_costs.day` | Tokens for a 24-hour MOTD (default `100`) |
| `motd_costs.week` | Tokens for a 7-day MOTD (default `500`) |
| `motd_costs.month` | Tokens for a 30-day MOTD (default `1500`) |
