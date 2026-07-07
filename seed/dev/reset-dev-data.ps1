$ErrorActionPreference = "Stop"

$scripts = @(
  @{ Container = "movie_db"; User = "movie_user"; Database = "movie_db"; File = "movie-data.sql" },
  @{ Container = "show_db"; User = "show_user"; Database = "show_db"; File = "show-data.sql" },
  @{ Container = "auth_db"; User = "auth_user"; Database = "auth_db"; File = "auth-data.sql" },
  @{ Container = "user_db"; User = "user_user"; Database = "user_db"; File = "user-data.sql" },
  @{ Container = "reservation_db"; User = "reservation_user"; Database = "reservation_db"; File = "reservation-data.sql" }
)

$queues = @(
  "user.registration.queue",
  "ticket.reservation.queue",
  "ticket.update.queue",
  "reservation.update.queue",
  "payment.process.queue"
)

foreach ($queue in $queues) {
  Write-Host "Purging RabbitMQ queue $queue..."
  docker exec rabbitmq rabbitmqctl purge_queue $queue
  if ($LASTEXITCODE -ne 0) {
    throw "Failed to purge RabbitMQ queue $queue"
  }
}

foreach ($script in $scripts) {
  $path = Join-Path $PSScriptRoot $script.File
  if (-not (Test-Path $path)) {
    throw "Missing seed file: $path"
  }

  Write-Host "Applying $($script.File) to $($script.Container)..."
  Get-Content -Raw $path | docker exec -i $script.Container psql -U $script.User -d $script.Database
  if ($LASTEXITCODE -ne 0) {
    throw "Failed to apply $($script.File) to $($script.Container)"
  }
}

Write-Host "Dev data reset complete."
