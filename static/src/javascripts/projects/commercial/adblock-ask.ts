import config from '../../lib/config';
import fastdom from '../../lib/fastdom-promise';
import { pageShouldHideReaderRevenue } from '../common/modules/commercial/contributions-utilities';
import { supportSubscribeDigitalURL } from '../common/modules/commercial/support-utilities';
import { shouldHideSupportMessaging } from '../common/modules/commercial/user-features';

const params = new URLSearchParams();
params.set(
	'acquisitionData',
	JSON.stringify({
		componentType: 'ACQUISITIONS_OTHER',
		source: 'GUARDIAN_WEB',
		campaignCode: 'shady_pie_open_2019',
		componentId: 'shady_pie_open_2019',
	}),
);
params.set('INTCMP', 'shady_pie_open_2019');

const supportUrl = `${supportSubscribeDigitalURL()}?${params.toString()}`;

const askHtml = `
<div class="contributions__adblock">
    <a href="${supportUrl}">
        <img src="https://uploads.guim.co.uk/2020/10/02/Digisubs_MPU_c1_my_opt.png" width="300" alt="" />
    </a>
</div>
`;

const canShow = (): boolean =>
	!shouldHideSupportMessaging() &&
	!pageShouldHideReaderRevenue() &&
	!config.get('page.hasShowcaseMainElement');

export const initAdblockAsk = (): Promise<void> => {
	if (!canShow()) return Promise.resolve();

	return fastdom
		.measure(() => document.querySelector('.js-aside-slot-container'))
		.then((slot) => {
			if (!slot) return;
			return fastdom.mutate(() => {
				slot.insertAdjacentHTML('beforeend', askHtml);
			});
		});
};

export const _ = {
	params,
	canShow,
};