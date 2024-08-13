from mcresources import ResourceManager

import constants


def generate(rm: ResourceManager) -> None:
    rm.item("unfinished_leather_sack").with_item_model()
    rm.item("reinforced_fiber").with_item_model()
    rm.item("reinforced_fabric").with_item_model()
    rm.item("pack_frame").with_item_model()

    # Sacks
    for sack in constants.SACKS:
        rm.item(sack).with_item_model()
