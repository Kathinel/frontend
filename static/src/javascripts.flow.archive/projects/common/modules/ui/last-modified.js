/**
 * DO NOT EDIT THIS FILE
 *
 * It is not used to to build anything.
 *
 * It's just a record of the old flow types.
 *
 * Use it as a guide when converting
 * - static/src/javascripts/projects/common/modules/ui/last-modified.js
 * to .ts, then delete it.
 */

// @flow

import fastdom from 'lib/fastdom-promise';

const lastModified = (): void => {
    fastdom
        .measure(() => ({
            lastModifiedElm: document.querySelector('.js-lm'),
            webPublicationDateElm: document.querySelector('.js-wpd'),
        }))
        .then(els => {
            const { lastModifiedElm, webPublicationDateElm } = els;

            if (lastModifiedElm && webPublicationDateElm) {
                fastdom.mutate(() => {
                    webPublicationDateElm.classList.add(
                        'content__dateline-wpd--modified'
                    );
                });

                webPublicationDateElm.addEventListener('click', () => {
                    fastdom.mutate(() => {
                        lastModifiedElm.classList.toggle('u-h');
                    });
                });
            }
        });
};

export { lastModified };