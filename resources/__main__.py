from mcresources import ResourceManager

import assets
import data
import recipes
import tags


def main() -> None:
    resourceManager = ResourceManager("sns", "./src/main/resources",
                                      on_error=lambda file, e: print(f"Error writing {file}\n{e}"))

    print("Starting resource generation.")
    assets.generate(resourceManager)
    data.generate(resourceManager)
    tags.generate(resourceManager)
    recipes.generate(resourceManager)
    resourceManager.flush()

    print(f"Finished generating files!")
    print(f"New: {resourceManager.new_files}, Modified: {resourceManager.modified_files},"
          f" Unchanged: {resourceManager.unchanged_files}, Errors: {resourceManager.error_files}")

    assets.generate(resourceManager)


if __name__ == '__main__':
    main()
