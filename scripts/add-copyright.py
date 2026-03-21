#!/usr/bin/env python3
import click
import os
import subprocess
from pathlib import Path


def gen_command(fullpath: Path, force: bool, name: str):
    command = [
        "uv",
        "run",
        "reuse",
        "annotate",
        str(fullpath),
        "--copyright=Kokoroid Contributors",
        "--license=LGPL-2.1",
    ]
    if name:
        command.append(f'--contributor={name}')
    if not force:
        command.append("--skip-existing")
    return str(fullpath), command


def commands(force: bool, name: str):
    modules = [
        Path("core"),
        Path("core-api"),
        Path("driver-api"),
        Path("adapter-api"),
        Path("plugin-api"),
        Path("test-extension"),
        Path("transport-api"),

    ]
    for module in modules:
        for item in (module / "src" / "main", module / "src" / "test"):
            for file in item.rglob("*.kt"):
                fullpath = file.resolve()
                yield gen_command(fullpath, force, name)


@click.command()
@click.option("-n", "--name", help="contributor name")
@click.option("-f", "--force", is_flag=True, help="force add copyright headers without confirmation")
@click.option("--debug", is_flag=True, help="enable debug mode")
def main(name, force=False, debug=False):
    if name is None:
        if not click.confirm(
                f'No contributor name was provided. Only the default copyright header will be added. This is HARD to change later. Continue?'):
            click.echo("Aborted")
            click.echo("use `uv run ./scripts/add-copyright.py --name <YOUR_NAME>` to set your name!")
            raise click.Abort()
    elif not click.confirm(
            f'You are about to add contributor name "{name}" to copyright headers. This is hard to change later. Continue?'):
        click.echo("Aborted")
        click.echo("use `uv run ./scripts/add-copyright.py --name <YOUR_NAME>` to set correct name!")
        raise click.Abort()
    if force and not click.confirm(
            f'FORCE mode is enabled. This will overwrite ALL existing copyright headers. ARE YOU SURE?'):
        click.echo("Aborted")
        click.echo("remove -f or --force flag to disable it!")
        raise click.Abort()

    click.echo(f"Adding copyright headers {f"for {name}" if name else ""}......")
    success = []
    failed = []
    for command in commands(force, name):
        if debug:
            print(f"adding header for {command[0]} ({" ".join(command[1])})")
        result = subprocess.run(command[1], capture_output=True, text=True)
        click.echo(result.stdout, nl=False)
        if result.returncode != 0:
            click.echo("ERROR:")
            if result.stderr:
                click.echo(result.stderr)
            failed.append(command[0])
        else:
            success.append(command[0])
    if failed:
        click.echo("Failed:")
        for item in failed:
            click.echo(item)
    elif debug:
        click.echo("Successed:")
        click.echo(success)
    click.echo(f"Success: {len(success)}")
    click.echo(f"Failed: {len(failed)}")


if __name__ == "__main__":
    main()
