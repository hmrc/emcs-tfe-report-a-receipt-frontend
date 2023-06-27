/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fixtures.messages

object ConfirmationMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val reference: String => String
    val whatNextH2: String
    val whatNextP1: String
    val whatNextP2: String
    val refusedH3: String
    val refusedP1: String
    val refusedBullet1: String
    val refusedBullet2: String
    val refusedBullet3: String
    val shortageH3: String
    val shortageP1: String
    val excessH3: String
    val excessP1: String
    val excessSameGoodsBullet1: String
    val excessSameGoodsBullet2: String
    val excessP2: String
    val excessDifferentGoodsBullet1: String
    val excessDifferentGoodsBullet2: String
    val contactHmrc: String
    val returnToAtAGlance: String
    val feedback: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title: String = title("Report of receipt submitted")
    override val heading = "Report of receipt submitted"
    override val reference: String => String = "Your submission reference is " + _
    override val whatNextH2 = "What happens next"
    override val whatNextP1 = "The movement will be updated to show you have successfully submitted a report of receipt. This may not happen straight away."
    override val whatNextP2 = "Print this screen to make a record of your submission."
    override val refusedH3 = "If you have refused or partially refused this movement"
    override val refusedP1 = "The consignor may now need to:"
    override val refusedBullet1 = "complete a change of destination (CoD) to move the refused goods to another authorised consignee"
    override val refusedBullet2 = "split the movement (for energy products only), or"
    override val refusedBullet3 = "complete a cancellation (if the goods have not left the place of dispatch)"
    override val shortageH3 = "If you recorded a shortage"
    override val shortageP1 = "A duty point may be created for any goods in this movement that are short. This means HMRC will require duty to be paid on those goods by the person or organisation that is holding them."
    override val excessH3 = "If you recorded an excess"
    override val excessP1 = "When the excess goods match whats on the electronic administrative document (eAD) you must:"
    override val excessSameGoodsBullet1 = "keep a full audit trail of the onward movement of goods you do not keep"
    override val excessSameGoodsBullet2 = "accept the duty liability for any goods you keep"
    override val excessP2 = "When the excess goods do not match whats on the eAD:"
    override val excessDifferentGoodsBullet1 = "a duty point will be created for those goods"
    override val excessDifferentGoodsBullet2 = "the person or organisation holding the goods must immediately pay the duty on them"
    override val contactHmrc = "Contact the HMRC excise helpline (opens in new tab) if you need more help or information about excise duties."
    override val returnToAtAGlance = "Return to at a glance"
    override val feedback = "What did you think of this service? (opens in new tab) (takes 30 seconds)"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val title: String = title("Report of receipt submitted")
    override val heading = "Report of receipt submitted"
    override val reference: String => String = "Your submission reference is " + _
    override val whatNextH2 = "What happens next"
    override val whatNextP1 = "The movement will be updated to show you have successfully submitted a report of receipt. This may not happen straight away."
    override val whatNextP2 = "Print this screen to make a record of your submission."
    override val refusedH3 = "If you have refused or partially refused this movement"
    override val refusedP1 = "The consignor may now need to:"
    override val refusedBullet1 = "complete a change of destination (CoD) to move the refused goods to another authorised consignee"
    override val refusedBullet2 = "split the movement (for energy products only), or"
    override val refusedBullet3 = "complete a cancellation (if the goods have not left the place of dispatch)"
    override val shortageH3 = "If you recorded a shortage"
    override val shortageP1 = "A duty point may be created for any goods in this movement that are short. This means HMRC will require duty to be paid on those goods by the person or organisation that is holding them."
    override val excessH3 = "If you recorded an excess"
    override val excessP1 = "When the excess goods match whats on the electronic administrative document (eAD) you must:"
    override val excessSameGoodsBullet1 = "keep a full audit trail of the onward movement of goods you do not keep"
    override val excessSameGoodsBullet2 = "accept the duty liability for any goods you keep"
    override val excessP2 = "When the excess goods do not match whats on the eAD:"
    override val excessDifferentGoodsBullet1 = "a duty point will be created for those goods"
    override val excessDifferentGoodsBullet2 = "the person or organisation holding the goods must immediately pay the duty on them"
    override val contactHmrc = "Contact the HMRC excise helpline (yn agor tab newydd) if you need more help or information about excise duties."
    override val returnToAtAGlance = "Return to at a glance"
    override val feedback = "What did you think of this service? (yn agor tab newydd) (takes 30 seconds)"
  }
}
