package pages

import pages.behaviours.PageBehaviours
import pages.unsatisfactory.individualItems.ItemShortageOrExcessPage

class ItemShortageOrExcessPageSpec extends PageBehaviours {

  "ItemShortageOrExcessPage" - {

    beRetrievable[Boolean](ItemShortageOrExcessPage)

    beSettable[Boolean](ItemShortageOrExcessPage)

    beRemovable[Boolean](ItemShortageOrExcessPage)
  }
}
