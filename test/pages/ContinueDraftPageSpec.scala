package pages

import pages.behaviours.PageBehaviours

class ContinueDraftPageSpec extends PageBehaviours {

  "ContinueDraftPage" - {

    beRetrievable[Boolean](ContinueDraftPage)

    beSettable[Boolean](ContinueDraftPage)

    beRemovable[Boolean](ContinueDraftPage)
  }
}
